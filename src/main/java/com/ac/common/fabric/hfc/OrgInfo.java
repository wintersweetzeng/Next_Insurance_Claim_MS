package com.ac.common.fabric.hfc;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.assertj.core.util.Sets;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import com.google.common.collect.Maps;

import lombok.Data;

@Data
public class OrgInfo {

	private String name;
	private String domainName;

	private String mspid;
	private HFCAClient caClient;

	private String caLocation;
	private Properties caProperties = null;

	private HFCUser admin;
	private HFCUser peerAdmin;
	private Map<String, User> userMap = Maps.newHashMap();

	private Map<String, String> peerLocations = Maps.newHashMap();
	private Map<String, String> ordererLocations = Maps.newHashMap();
	private Map<String, String> eventHubLocations = Maps.newHashMap();

	private Set<Peer> peers = Sets.newHashSet();

	public void addPeerLocation(String name, String location) {
		peerLocations.put(name, location);
	}

	public void addOrdererLocation(String name, String location) {
		ordererLocations.put(name, location);
	}

	public void addEventHubLocation(String name, String location) {
		eventHubLocations.put(name, location);
	}

	public String getPeerLocation(String name) {
		return peerLocations.get(name);
	}

	public String getOrdererLocation(String name) {
		return ordererLocations.get(name);
	}

	public String getEventHubLocation(String name) {
		return eventHubLocations.get(name);
	}

	public void addUser(User user) {
		userMap.put(user.getName(), user);
	}

	public User getUser(String name) {
		return userMap.get(name);
	}

	public void addPeer(Peer peer) {
		peers.add(peer);
	}

	public Collection<String> getOrdererLocations() {
		return Collections.unmodifiableCollection(ordererLocations.values());
	}

	public Collection<String> getEventHubLocations() {
		return Collections.unmodifiableCollection(eventHubLocations.values());
	}

	public Set<Peer> getPeers() {
		return Collections.unmodifiableSet(peers);
	}

	public Set<String> getPeerNames() {

		return Collections.unmodifiableSet(peerLocations.keySet());
	}

	public Set<String> getOrdererNames() {

		return Collections.unmodifiableSet(ordererLocations.keySet());
	}

	public Set<String> getEventHubNames() {

		return Collections.unmodifiableSet(eventHubLocations.keySet());
	}

}
