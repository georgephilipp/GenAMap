package JSci.maths.matrices;

import java.lang.ref.SoftReference;

/**
 *
 * @author Mark
 */
final class DoubleLUCache {
    final SoftReference lRef;
    final SoftReference uRef;
    final SoftReference pivotRef;

    DoubleLUCache(AbstractDoubleSquareMatrix l, AbstractDoubleSquareMatrix u, int[] pivot) {
        this.lRef = new SoftReference(l);
        this.uRef = new SoftReference(u);
        this.pivotRef = new SoftReference(pivot);
    }
    AbstractDoubleSquareMatrix getL() {
        return (AbstractDoubleSquareMatrix) lRef.get();
    }
    AbstractDoubleSquareMatrix getU() {
        return (AbstractDoubleSquareMatrix) uRef.get();
    }
    int[] getPivot() {
        return (int[]) pivotRef.get();
    }
}
