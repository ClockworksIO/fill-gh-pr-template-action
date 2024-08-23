FROM babashka/babashka:1.3.191-alpine

RUN apk add git --no-cache

#COPY entrypoint.sh /entrypoint.sh
#RUN chmod +x entrypoint.sh

COPY foo.md /foo.md

#COPY ../target/fill-gh-pr-template fill-gh-pr-template
#RUN chmod +x fill-gh-pr-template

#ENTRYPOINT ["/entrypoint.sh"]