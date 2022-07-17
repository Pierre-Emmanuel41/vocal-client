package fr.pederobien.vocal.client.impl;

import java.util.function.Consumer;

import fr.pederobien.messenger.interfaces.IResponse;
import fr.pederobien.sound.impl.SoundResourcesProvider;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.client.event.VocalMainPlayerDeafenStatusChangePreEvent;
import fr.pederobien.vocal.client.event.VocalMainPlayerNameChangePreEvent;
import fr.pederobien.vocal.client.event.VocalPlayerDeafenStatusChangePostEvent;
import fr.pederobien.vocal.client.event.VocalPlayerMuteStatusChangePostEvent;
import fr.pederobien.vocal.client.interfaces.IVocalMainPlayer;
import fr.pederobien.vocal.client.interfaces.IVocalServer;

public class VocalMainPlayer extends AbstractPlayer implements IVocalMainPlayer, IEventListener {

	/**
	 * Creates a player associated to a name and a server.
	 * 
	 * @param server The server on which this player is registered.
	 * @param name   The player name.
	 */
	protected VocalMainPlayer(IVocalServer server, String name) {
		super(server, name);

		EventManager.registerListener(this);
	}

	@Override
	public void setName(String name, Consumer<IResponse> callback) {
		if (getName().equals(name))
			return;

		EventManager.callEvent(new VocalMainPlayerNameChangePreEvent(this, name, callback));
	}

	@Override
	public void setDeafen(boolean isDeafen, Consumer<IResponse> callback) {
		if (isDeafen() == isDeafen)
			return;

		EventManager.callEvent(new VocalMainPlayerDeafenStatusChangePreEvent(this, isDeafen, callback));
	}

	@EventHandler
	private void onPlayerMuteChange(VocalPlayerMuteStatusChangePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		if (event.getPlayer().isMute())
			SoundResourcesProvider.getMicrophone().pause();
		else {
			SoundResourcesProvider.getMicrophone().resume();
		}
	}

	@EventHandler
	private void onPlayerDeafenChange(VocalPlayerDeafenStatusChangePostEvent event) {
		if (!event.getPlayer().equals(this))
			return;

		if (event.getPlayer().isDeafen()) {
			SoundResourcesProvider.getSpeakers().pause();
			SoundResourcesProvider.getMixer().clear();
		} else
			SoundResourcesProvider.getSpeakers().resume();
	}
}
