package edu.shaykemelov.protocols.http.state;

public interface HasState
{
	void nextState();

	void setNewState(State newState);
}