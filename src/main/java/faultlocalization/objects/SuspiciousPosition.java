package faultlocalization.objects;

/**
 * Information of detected suspicious code positions.
 * 
 * @author kui.liu
 *
 */
public class SuspiciousPosition implements Comparable <SuspiciousPosition> {
	public String classPath;
	public int lineNumber;
	public float ratio;
	
	@Override
	public int compareTo(SuspiciousPosition o) {
		return Float.compare(this.lineNumber, o.lineNumber);
	}
}
