package JSci.maths.matrices;

import java.lang.ref.SoftReference;

/**
 *
 * @author Mark
 */
final class ComplexLUCache {
    final SoftReference lRef;
    final SoftReference uRef;
    final SoftReference pivotRef;

    ComplexLUCache(AbstractComplexSquareMatrix l, AbstractComplexSquareMatrix u, int[] pivot) {
        this.lRef = new SoftReference(l);
        this.uRef = new SoftReference(u);
        this.pivotRef = new SoftReference(pivot);
    }
    AbstractComplexSquareMatrix getL() {
        return (AbstractComplexSquareMatrix) lRef.get();
    }
    AbstractComplexSquareMatrix getU() {
        return (AbstractComplexSquareMatrix) uRef.get();
    }
    int[] getPivot() {
        return (int[]) pivotRef.get();
    }
}
