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
	private boolean sputumClosed;
	private boolean xrayDone;
	private boolean xrayClosed;
	private boolean xraySkip;

	/** TB-positive enrolment: a sputum result, and an X-ray result or a recorded skip. */
	public boolean positiveReady() {
		return sputumDone && xraySettled();
	}

	/** Index or contact has been tested: sputum finished and X-ray finished or skipped. */
	public boolean tested() {
		return (sputumDone || sputumClosed) && xraySettled();
	}

	private boolean xraySettled() {
		return xrayDone || xrayClosed || xraySkip;
	}
}
