package MasterNode;

import java.sql.Timestamp;

public class Block {
	private String previousHash;
	private String hashBlock;
	private Timestamp timestamp;
	private int nonce;
	
	public Block(String previous_hash, String hash_block, Timestamp time, int nonce_block){
		previousHash = previous_hash;
		hashBlock = hash_block;
		timestamp = time;
		nonce = nonce_block;
	}
	public String getPreviousHash(){
		return previousHash;
	}

	public String getHashBlock() {
		return hashBlock;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public int getNonce() {
		return nonce;
	}
}
