#!/bin/bash
#


sudo networksetup -setwebproxy "Wi-Fi" 127.0.0.1 8090
sudo networksetup -setwebproxystate "Wi-Fi" on
# sudo networksetup -setwebproxystate "Wi-Fi" off
sudo networksetup -setsecurewebproxy "Wi-Fi" 127.0.0.1 8090
sudo networksetup -setsecurewebproxystate "Wi-Fi" on
# sudo networksetup -setsecurewebproxystate "Wi-Fi" off
