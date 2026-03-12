#!/bin/bash

export JAVA_HOME=$(/usr/libexec/java_home -v 17)

$JAVA_HOME/bin/java -jar KBU-Crack-1.0-SNAPSHOT.jar

read -p "프로그램이 종료되었습니다. 창을 닫으려면 엔터를 치세요..."