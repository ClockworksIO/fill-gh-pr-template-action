(ns action.core
  (:gen-class)
  (:require
    [clojure.string :as str]
    [babashka.process :refer [shell] :as process]))


(def github-workspace-path "/github/workspace")



(defn get-latest-commit-message
  [branch]
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
                "--name-only"
                "--decorate-refs-exclude=refs/tags"
                (format "--first-parent %s" branch)])]
     (as-> (shell {:out :string :dir github-workspace-path} cmd) $
           (:out $)
           (str/split $ #"!-M-!")
           (remove str/blank? $)
           (map #(str/split % #"(!-S-!)|(!-B-!)|(!-F-!)") $)
           (map (fn [[head subject body changes]]
                  (commit-col->map (apply conj [(splitter head) subject body (splitter changes)])))))))
                  


(defn -main [& args]
  (println "Hello from Clojure")
  (apply println args))