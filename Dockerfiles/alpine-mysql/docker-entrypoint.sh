#!/bin/sh
set -e

echo $0 start.

if [ ! -d "/run/mysqld" ]; then
  mkdir -p /run/mysqld
fi
chown -R mysql:mysql /run/mysqld

if [ -d /var/lib/mysql/mysql ]; then
  echo "[Note] MySQL directory already present, skipping creation"

  echo "[Note] chown /var/lib/mysql"
  chown -R mysql:mysql /var/lib/mysql || true
else
  echo "[Note] Initializing database files"

  echo "[Note] chown /var/lib/mysql"
  chown -R mysql:mysql /var/lib/mysql || true

  echo "[Note] Check if the root password option exists"
  if [ -z "$MYSQL_ROOT_PASSWORD" -a -z "$MYSQL_RANDOM_ROOT_PASSWORD" ]; then
    echo "[ERROR] Database is uninitialized and password option is not specified\n\tYou need to specify one of MYSQL_ROOT_PASSWORD and MYSQL_RANDOM_ROOT_PASSWORD"
  fi

  echo "[Note] Exec mysql_install_db"
  mysql_install_db --user=mysql --ldata=/var/lib/mysql > /dev/null

  if [ -n "$MYSQL_RANDOM_ROOT_PASSWORD" ]; then
    echo "[Note] Generate root password"
    export MYSQL_ROOT_PASSWORD="$(pwgen -1 32)"
    echo "[Note] GENERATED ROOT PASSWORD: $MYSQL_ROOT_PASSWORD"
  fi

  MYSQL_DATABASE=${MYSQL_DATABASE:-""}
  MYSQL_USER=${MYSQL_USER:-""}
  MYSQL_PASSWORD=${MYSQL_PASSWORD:-""}

  tfile=`mktemp`
  if [ ! -f "$tfile" ]; then
      echo "[Note] test -f $tfile failed"
      return 1
  fi

  echo "[Note] Generate initialization file"
  cat << EOF > $tfile
USE mysql;
FLUSH PRIVILEGES;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY "$MYSQL_ROOT_PASSWORD" WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY "$MYSQL_ROOT_PASSWORD" WITH GRANT OPTION;
SET PASSWORD FOR 'root'@'localhost'=PASSWORD('${MYSQL_ROOT_PASSWORD}') ;
DROP DATABASE IF EXISTS test ;
FLUSH PRIVILEGES ;
EOF

  if [ "$MYSQL_DATABASE" != "" ]; then
    echo "[Note] Creating database $MYSQL_DATABASE"
    echo "CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE\` CHARACTER SET utf8 COLLATE utf8_general_ci;" >> $tfile

    if [ "$MYSQL_USER" != "" ]; then
      echo "[Note] Creating user $MYSQL_USER with password $MYSQL_PASSWORD"
      echo "GRANT ALL ON \`$MYSQL_DATABASE\`.* to '$MYSQL_USER'@'%' IDENTIFIED BY '$MYSQL_PASSWORD';" >> $tfile
    fi
  fi

  echo "[Note] Exec initialization file"
  /usr/bin/mysqld --user=mysql --bootstrap --verbose=0 --skip-name-resolve --skip-networking=0 < $tfile

  echo "[Note] Remove initialization file"
  rm -f $tfile
fi

echo $0 end.

# exec cmd
echo "[Note] Exec mysqld"
exec /usr/bin/mysqld --user=mysql --console --skip-name-resolve --skip-networking=0
