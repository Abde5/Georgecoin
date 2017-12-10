package MasterNode;

import java.sql.Timestamp;

public class Block {
	private String previousHash;
	private String hashBlock;
	private Timestamp timestamp;
	private int nonce;
	private String Tx0;
	private String Tx1;
	private String Tx2;
	private String Tx3;
	
	public Block(String previous_hash, String hash_block, Timestamp time, int nonce_block, String t0, String t1, String t2, String t3){
		previousHash = previous_hash;
		hashBlock = hash_block;
		timestamp = time;
		nonce = nonce_block;
		Tx0 = t0;
		Tx1 = t1;
		Tx2 = t2;
		Tx3 = t3;
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

	public String getTx0(){
		return Tx0;
	}

	public String getTx1(){
		return Tx1;
	}

	public String getTx2(){
		return Tx2;
	}

	public String getTx3(){
		return Tx3;
	}
}

