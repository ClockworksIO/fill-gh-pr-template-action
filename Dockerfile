FROM babashka/babashka:1.3.191-alpine

RUN apk add git --no-cache

COPY entrypoint.sh /entrypoint.sh

COPY action/target/fill-gh-pr-template /action/bin/fill-gh-pr-template
RUN chmod +x /action/bin/fill-gh-pr-template

ENTRYPOINT ["/entrypoint.sh"]