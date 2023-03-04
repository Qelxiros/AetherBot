# AetherBot
mfw containerized word bot (wip (fully functional now tho))

# Download
idk git clone or smth
you prob know how to do this already

# Dependencies
- [podman](https://podman.io/)
- any VNC client (I like [TurboVNC](https://www.turbovnc.org/) (the instructions below will use TurboVNC))

# Usage
1. build the image from the Dockerfile
```
cd AetherBot
podman build -t test .
```
2. run the container from the image
```
podman run --rm -itd -p 5900:5900 --shm-size 2g test
```
3. Use your vnc client to connect to the running container
```
/opt/TurboVNC/vncviewer
```
When prompted, enter `hostname:5900` and click connect, where hostname is the hostname of the machine running the container

4. In the container, open a terminal by right-clicking and selecting Applications > Shells > Bash, then repeat the process so you have two terminal windows

5. use one terminal to start google chrome in the container (errors are expected)
```
google-chrome --remote-debugging-port=9222
```
6. In the other terminal, start the selenium program
```
java -jar AetherBot.jar
```
7. the chrome window should now have a tab for jklm.fun. navigate to a room of your choice and enjoy!
