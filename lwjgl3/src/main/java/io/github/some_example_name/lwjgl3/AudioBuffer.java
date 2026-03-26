package io.github.some_example_name.lwjgl3;

public class AudioBuffer {

	private final byte[] data;

	public AudioBuffer() {
		this.data = new byte[0];
	}

	public AudioBuffer(byte[] data) {
		this.data = (data != null) ? data : new byte[0];
	}

	public byte[] getData() {
		return data;
	}

	public int getSize() {
		return data.length;
	}

	public boolean isEmpty() {
		return data.length == 0;
	}
}
