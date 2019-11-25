package ca.mcgill.ecse429.mutation;

public class MutantInformation {
	public static String FILE_HEADER = "Id, Line, CharNumber, Original, Mutant";
	
	private int id;
	private int lineNumber;
	private int charNumber;
	private String originalInfo;
	private String mutantInfo;
	
	public MutantInformation(int lineNumber, int charNumber, String originalInfo, String mutantInfo) {
		this.lineNumber = lineNumber;
		this.charNumber = charNumber;
		this.originalInfo = originalInfo;
		this.mutantInfo = mutantInfo;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getCharNumber() {
		return charNumber;
	}

	public String getOriginalInfo() {
		return originalInfo;
	}

	public String getMutantInfo() {
		return mutantInfo;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {		
		return id + ", " + lineNumber + ", " + charNumber + ", " + originalInfo + ", " + mutantInfo;
	}
}
