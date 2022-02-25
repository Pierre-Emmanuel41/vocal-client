# Presentation

This project is an implementation of a vocal client. It integrates the project [sound](https://github.com/Pierre-Emmanuel41/sound) in order to get access to the player microphone and speakers.

# Download

First you need to download this project on your computer. To do so, you can use the following command line :

```git
git clone -b 1.0-SNAPSHOT https://github.com/Pierre-Emmanuel41/vocal-client.git --recursive
```

and then double click on the deploy.bat file. This will deploy this project and all its dependencies on your computer. Which means it generates the folder associated to this project and its dependencies in your .m2 folder. Once this has been done, you can add the project as maven dependency on your maven project :

```xml
<dependency>
	<groupId>fr.pederobien</groupId>
	<artifactId>vocal-client</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

# Tutorial

This project provides only one interface and one implementation of this interface : [IVocalClient](https://github.com/Pierre-Emmanuel41/vocal-client/blob/1.0-SNAPSHOT/src/main/java/fr/pederobien/vocal/client/interfaces/IVocalClient.java) and <code>VocalClient</code>.

Example:

```java
// Client name
String name = "Player 1";

// Server address
String address = "127.0.0.1";

// Server port number
int port = 25000;

// Instantiating a vocal client
IVocalClient client = new VocalClient(name, address, port);

// When the connection completes, the microphone and the speakers are automatically started
// But it is possible to cancel by handling the MicrophoneStartPreEvent and SpeakersStartPreEvent:
EventListener listener = new EventListener(client);
EventManager.registerListener(listener);

// Connecting the client to the server
client.connect(); // Microphone and speakers won't be started

EventManager.unregisterListener(listener);

// Events MicrophoneStartPreEvent and SpeakersStartPreEvent will be thrown but not cancelled.
client.getSoundResourceProvider().getMicrophone().start();
client.getSoundResourceProvider().getSpeakers().start();



public class EventListener implements IEventListener {
	private IVocalClient client;
	
	public EventListener(IVocalClient client) {
		this.client = client;
	}
	
	@EventHAndler
	private void onMicroStartPreEvent(MicrophoneStartPreEvent event) {
		if (!event.getMicrophone().equals(client.getSoundResourceProvider().getMicrophone()))
			return;
		
		event.setCancelled(true);
	}
	
	@EventHAndler
	private void onSpeakersStartPreEvent(SpeakersStartPreEvent event) {
		if (!event.getSpeakers().equals(client.getSoundResourceProvider().getSpeakers()))
			return;
		
		event.setCancelled(true);
	}
}
```