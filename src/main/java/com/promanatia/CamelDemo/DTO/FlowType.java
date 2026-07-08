package com.promanatia.CamelDemo.DTO;

public enum FlowType {

	SalesOrder("SalesOrder", "c_order", "SalesOrder"),

	SalesInvoice("SalesInvoice", "c_invoice", "SalesInvoice"),

	PurchaseOrder("PurchaseOrder", "c_purchaseorder", "PurchaseOrder"),

	PurchaseInvoice("PurchaseInvoice", "c_purchaseinvoice", "PurchaseInvoice");

	private final String prefix;
	private final String sourceTable;
	private final String outputFileName;

	FlowType(String prefix, String sourceTable, String outputFileName) {

		this.prefix = prefix;
		this.sourceTable = sourceTable;
		this.outputFileName = outputFileName;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSourceTable() {
		return sourceTable;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public static FlowType fromFileName(String fileName) {

		if (fileName == null || fileName.isBlank()) {
			throw new IllegalArgumentException("Filename cannot be null");
		}

		String rawPrefix = fileName.split("_")[0].trim();
		for (FlowType type : values()) {
			if (type.prefix.equalsIgnoreCase(rawPrefix) || type.name().equalsIgnoreCase(rawPrefix)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Unsupported file prefix : " + rawPrefix);
	}
}