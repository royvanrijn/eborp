tshark -l -i wlan0mon -R "wlan.fc.subtype == 4" -T fields -E separator="," -e frame.time_epoch -e wlan.sa -e radiotap.dbm_antsignal
#tshark -l -i wlan0mon -f "broadcast" -R "wlan.fc.type == 0 && wlan.fc.subtype == 4" -T fields -E separator="," -e frame.time_epoch -e wlan.sa -e radiotap.dbm_antsignal
