echo fuck &
Xvfb :0 &
echo fuck &
/usr/bin/x11vnc -forever -display :0 -usepw -listen tcp -listen inet -listen inet6 -listen unix -listen local &
echo fuck &
xterm /bin/bash &
echo fuck
