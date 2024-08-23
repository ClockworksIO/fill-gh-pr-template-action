FROM ghcr.io/clockworksio/fill-gh-pr-template-action:latest

RUN apk add git --no-cache

COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]