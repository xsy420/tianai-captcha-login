FROM alpine

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.tuna.tsinghua.edu.cn/g' /etc/apk/repositories && \
apk add --no-cache redis && sed -i "s/protected-mode yes/protected-mode no/" /etc/redis.conf

CMD ["redis-server", "--protected-mode", "no"]
EXPOSE 6379

