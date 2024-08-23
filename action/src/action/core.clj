(ns action.core
  (:gen-class)
  (:require
    [clojure.string :as str]
    [clci.conventional-commit :as cc]
    [babashka.process :refer [shell] :as process]))


(def github-workspace-path "/github/workspace")



(defn get-latest-commit-message
  "Get the commit message of the latest commit.
   Takes the path `dir` to the root of the local repository and the name of the
   `branch` for wich to get the commit message."
  [dir branch]
  (let [;; sanitize a single log entry into a map
         ;; used after grouping the log lines into commits
         commit-col->map   (fn [v]
                             {:hash    (get v 0)
                              :date    (get v 1)
                              :author  (get v 2)
                              :subject (get v 3)
                              :body    (get v 4)
                              :files   (get v 5)})
         ;; split entries on linebreak and sanitize whitespace
         splitter (comp (partial into []) #(remove str/blank? %) str/split-lines)
         ;; git shell command used to get the log in the format required
         cmd (str/join
               " "
               ["git log --format=\"%n!-M-!%H%n%ai%n%ae!-S-!%s!-B-!%b!-F-!\""
                "-n 1"
                "--name-only"
                "--decorate-refs-exclude=refs/tags"
                (format "--first-parent %s" branch)])]
     (as-> (shell {:out :string :dir dir} cmd) $
           (:out $)
           (str/split $ #"!-M-!")
           (remove str/blank? $)
           (map #(str/split % #"(!-S-!)|(!-B-!)|(!-F-!)") $)
           (map (fn [[head subject body changes]]
                  (commit-col->map (apply conj [(splitter head) subject body (splitter changes)]))) $)
       )
    ))


(defn get-current-branch-name
  "Get the name of the currently checked out branch.
   Takes the path `dir` to the root of the local repository."
  [dir]
  (-> (shell {:out :string :dir dir} "git rev-parse --abbrev-ref HEAD")
    :out
    (str/trim)
    )
  )




(defn- concat-msg
  "Assemble the full commit message of the given commit.
   Takes the commit headline and body and joins them to get a full commit
   message as it is expected by the cc parser."
  [commit]
  (str (:subject commit) "\n\n" (:body commit)))


(defn get-latest-commit-msg-ast
  "Get the ast of the latest commit message on the current branch.
   Takes the path `dir` to the root of the local repository."
  [dir]
  (->> (get-current-branch-name dir)
  (get-latest-commit-message dir)
  first
  concat-msg
  (cc/msg->ast )
  )
)
(comment 
  (get-latest-commit-msg-ast ".")
)



(defn -main [& _]
  (let [gh-token      (System/getenv "GITHUB_TOKEN")
        pr-id         (System/getenv "INPUT_PULL_REQUEST")
        pr-id-alt     (System/getenv "INPUT_PULL-REQUEST")
        draft?        (System/getenv "INPUT_DRAFT")
        draft-label   (System/getenv "INPUT_DRAFT_LABEL")
       ]
    (println "Updating the PR")
    (println "PR: " pr-id " | " pr-id-alt)
    (println "Github Token:" gh-token)
    (println "Draft? " draft? " with label: " draft-label)
    (println "Commit Data: " (get-latest-commit-msg-ast github-workspace-path))
    )
  )
