ffmpeg -i %1 -f %2 - | lame -ab %3 - encoded.mp3

echo "done"

dir