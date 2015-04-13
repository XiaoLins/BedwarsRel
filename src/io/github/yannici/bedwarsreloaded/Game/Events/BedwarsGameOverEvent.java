package io.github.yannici.bedwarsreloaded.Game.Events;

import io.github.yannici.bedwarsreloaded.Game.Game;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsGameOverEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Game game = null;
	
	public BedwarsGameOverEvent(Game game) {
		this.game = game;
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsGameOverEvent.handlers;
	}
	
	public static HandlerList getHandlerList() {
		return BedwarsGameOverEvent.handlers;
	}
	
	public Game getGame() {
		return this.game;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

}
