ffmpeg -i encoded.mp3 -f wav - | lame -ab $1 - decoded.mp3

echo "done"

dir

