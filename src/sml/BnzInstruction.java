package sml;

/**
 * This class ....
 * 
 * @author someone
 */

public class BnzInstruction extends Instruction {

	private int op1;
	private String nextLabel;

	public BnzInstruction(String label, String op) {
		super(label, op);
	}

	public BnzInstruction(String label, int result, int op1, String nextLabel) {
		this(label, "bnz");
		this.op1 = op1;
		this.nextLabel = nextLabel;
	}

	@Override
	public void execute(Machine m) {
		
		if (m.getRegisters().getRegister(op1) != 0) {
			int pcCounter = 0;
			for (Instruction inst : m.getProg()) {
				if (inst.label.equals(nextLabel)) {
					m.setPc(pcCounter);
					break;
				}
				++pcCounter;
			}
		}
	}

	@Override
	public String toString() {
		return super.toString() + " " + nextLabel + " next to execute";
	}
}
