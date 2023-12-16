FROM alpine

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
apk add --no-cache mysql

# entry point
COPY docker-entrypoint.sh /usr/local/bin

VOLUME ["/var/lib/mysql"]

EXPOSE 3306

ENTRYPOINT ["docker-entrypoint.sh"]
