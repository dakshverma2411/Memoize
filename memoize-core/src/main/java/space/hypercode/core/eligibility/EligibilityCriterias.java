package space.hypercode.core.eligibility;

/**
 * Provides shared singleton instances of built-in {@link EligibilityCriteria} implementations.
 */
public class EligibilityCriterias {
    /** Criteria that always allows caching, regardless of the return value. */
    public static final MemoizeAlways ALWAYS = MemoizeAlways.getInstance();
    /** Criteria that only caches non-null return values. */
    public static final MemoizeNonNulls ONLY_NON_NULLS = MemoizeNonNulls.getInstance();
}
