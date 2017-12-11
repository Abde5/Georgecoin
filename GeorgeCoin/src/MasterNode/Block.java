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
	
	/**
	 * Block constructor
	 * @param previous_hash
	 * @param hash_block
	 * @param time
	 * @param nonce_block
	 * @param t0
	 * @param t1
	 * @param t2
	 * @param t3
	 */
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
	
	/**
	 * previous hash getter
	 * @return String previousHash
	 */
	public String getPreviousHash(){
		return previousHash;
	}

	/**
	 * hash getter
	 * @return String hashBlock
	 */
	public String getHashBlock() {
		return hashBlock;
	}

	/**
	 * timestamp getter
	 * @return String timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * nonce getter
	 * @return int nonce
	 */
	public int getNonce() {
		return nonce;
	}

	/**
	 * Transaction 0 getter
	 * @return String Tx0
	 */
	public String getTx0(){
		return Tx0;
	}

	/**
	 * Transaction 1 getter
	 * @return String Tx1
	 */
	public String getTx1(){
		return Tx1;
	}

	/**
	 * Transaction 2 getter
	 * @return String Tx2
	 */
	public String getTx2(){
		return Tx2;
	}

	/**
	 * Transaction 3 getter
	 * @return String Tx3
	 */
	public String getTx3(){
		return Tx3;
	}
}

