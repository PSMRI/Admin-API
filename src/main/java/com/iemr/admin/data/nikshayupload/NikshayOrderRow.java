package com.iemr.admin.data.nikshayupload;

import lombok.AllArgsConstructor;
import lombok.Data;

/** One tb_diagnostic_order row the upload uses to decide whether to push a test. */
@Data
@AllArgsConstructor
public class NikshayOrderRow {

	private Long beneficiaryId;
	private String orderType;
	private String status;
	private String externalOrderId;
	private String reasonToClose;
}
