package sml;

/**
 * This class ....
 * 
 * @author someone
 */

public class BnzInstruction extends Instruction {

	private int register;
	private String nextLabel;

	public BnzInstruction(String label, String op) {
		super(label, op);
	}

	public BnzInstruction(String label, int register, String nextLabel) {
		this(label, "bnz");
		this.register = register;
		this.nextLabel = nextLabel;
	}

	@Override
	public void execute(Machine m) {
		
		int index = m.getLabels().indexOf(nextLabel);

		if(m.getRegisters().getRegister(register) != 0) {
			m.setPc(index);
		}
	}

	@Override
	public String toString() {
		return super.toString() + " " + nextLabel + " next to execute";
	}
}
