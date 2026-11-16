package com.iemr.admin.data.nikshayidgenerate;

import lombok.Data;

/**
 * Whether a beneficiary's chest X-ray and sputum (TrueNat) are finished in AMRIT.
 * Closed or refused orders count as finished and are not pushed. A chest X-ray whose
 * reason says pregnancy or child also counts as finished.
 */
@Data
public class NikshayDiagnosisGate {

	private boolean sputumDone;
	// TB positive by any source index eligibility uses (confirmed, form, suspected or NAAT order)
	private boolean tbPositive;
	private boolean sputumClosed;
	private boolean xrayDone;
	private boolean xrayClear;
	private boolean xrayClosed;
	private boolean xraySkip;

	/** TB-positive enrolment: a sputum result, and an X-ray result or a recorded skip. */
	public boolean positiveReady() {
		return sputumDone && xraySettled();
	}

	/**
	 * What a household member still waits for before the index patient's contacts are
	 * added, or null when nothing. A negative chest X-ray needs no sputum. A TB Presumptive,
	 * closed, refused or skipped X-ray needs a sputum result (a closed or refused sputum
	 * order keeps waiting).
	 */
	public String contactWaitsFor() {
		if (!xraySettled()) {
			return "X-ray";
		}
		if (xrayClear || sputumDone) {
			return null;
		}
		return "NAAT result";
	}

	private boolean xraySettled() {
		return xrayDone || xrayClosed || xraySkip;
	}
}
