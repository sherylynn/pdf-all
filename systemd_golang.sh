#!/bin/bash

sudo tee /etc/systemd/system/pdfall.service <<-'EOF'
[Unit]
Description=pdf-all Service
After=network-online.target
Wants=network-online.target systemd-networkd-wait-online.service

[Service]
EOF

sudo tee -a /etc/systemd/system/pdfall.service <<EOF
User=$(whoami)
;Group=$(id -g -n)
Type=simple
;ExecStartPre=/bin/bash --login -c 'env > /tmp/.new-env-file'
;EnvironmentFile=$HOME/.env.file
PrivateTmp=true
Restart=on-abnormal
ExecStart=$(cd "$(dirname "$0")";pwd)/server_golang.sh
EOF

sudo tee -a /etc/systemd/system/pdfall.service <<-'EOF'
; Hide /home, /root, and /run/user. Nobody will steal your SSH-keys.
;ProtectHome=true
;after protect even if this script can not read home
; Make /usr, /boot, /etc and possibly some more folders read-only.
ProtectSystem=full

[Install]
WantedBy=multi-user.target
EOF
sudo systemctl daemon-reload
sudo systemctl enable pdfall.service
sudo systemctl restart pdfall.service
sudo systemctl status pdfall.service
