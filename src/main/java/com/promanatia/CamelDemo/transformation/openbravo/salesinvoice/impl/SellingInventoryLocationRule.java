package com.promanatia.CamelDemo.transformation.openbravo.salesinvoice.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.promanatia.CamelDemo.DTO.FieldMappingDTO;
import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;
import com.promanatia.CamelDemo.repository.OrgSubsidaryRepository;
import com.promanatia.CamelDemo.service.LookupService;
import com.promanatia.CamelDemo.transformation.TransformationRule;
import com.promanatia.CamelDemo.utility.RowContext;

@Component
public class SellingInventoryLocationRule implements TransformationRule {

	private static final List<String> AMAZON_BUSINESS_PARTNERS = List.of("Amazon Advantage", "Amazon Seller Central",
			"Amazon Kindle Direct Publishing");

	private final LookupService lookupService;
	private final OrgSubsidaryRepository orgRepository;

	public SellingInventoryLocationRule(LookupService lookupService, OrgSubsidaryRepository orgRepository) {

		this.lookupService = lookupService;
		this.orgRepository = orgRepository;
	}

	@Override
	public String getRuleCode() {
		return "SELLING_INVENTORY_LOCATION";
	}

	@Override
	public String transform(RowContext row, FieldMappingDTO mapping) {

		String organization = row.get("organization");
		String productCategory = row.get("ProductcatParent");
		String businessPartner = row.get("BusinessPartnerExtID");

		/*
		 * Special handling for Aksharpith Online / Aksharpith - National
		 */
		if ("Aksharpith Online".equalsIgnoreCase(organization)
				|| "Aksharpith - National".equalsIgnoreCase(organization)) {

			// Aksharpith Online - Herbal
			if ("Aksharpith Online".equalsIgnoreCase(organization) && "10000".equalsIgnoreCase(productCategory)) {

				return getInventoryLocation("Aksharpith Online", "Aksharpith - NAHQ - Herbal - BAPS - WH");
			}

			// Aksharpith Online - Gifts
			if ("Aksharpith Online".equalsIgnoreCase(organization)) {

				return getInventoryLocation("Aksharpith Online", "Aksharpith - NAHQ - Gifts - BAPS - WH");
			}

			// Aksharpith National - Akshar.org Online
			if ("Aksharpith - National".equalsIgnoreCase(organization)
					&& "Akshar.org Online".equalsIgnoreCase(businessPartner)) {

				return getInventoryLocation("Aksharpith - National", "Aksharpith - NAHQ - Herbal - Akshar - WH");
			}

			// Aksharpith National - Amazon Herbal
			if ("Aksharpith - National".equalsIgnoreCase(organization) && "10000".equalsIgnoreCase(productCategory)
					&& isAmazonBusinessPartner(businessPartner)) {

				return getInventoryLocation("Aksharpith - National", "Aksharpith - NAHQ - Herbal - AMZN - WH");
			}

			// Aksharpith National - Amazon Gifts
			if ("Aksharpith - National".equalsIgnoreCase(organization) && isAmazonBusinessPartner(businessPartner)) {

				return getInventoryLocation("Aksharpith - National", "Aksharpith - NAHQ - Gifts - AMZN - WH");
			}
		}

		/*
		 * Default organization lookup
		 */
		OrgSubsidaryDto dto = row.getOrganization();

		if (dto == null && organization != null && !organization.isBlank()) {
			dto = lookupService.getOrganization(organization);
			row.setOrganization(dto);
		}

		return dto == null ? "" : dto.getExternalInventoryLocation();
	}

	private boolean isAmazonBusinessPartner(String businessPartner) {
		return businessPartner != null
				&& AMAZON_BUSINESS_PARTNERS.stream().anyMatch(v -> v.equalsIgnoreCase(businessPartner));
	}

	private String getInventoryLocation(String organization, String itemLineLocation) {

		OrgSubsidaryDto dto = orgRepository.findByOrganizationName(organization, itemLineLocation);

		return dto == null ? "" : dto.getExternalInventoryLocation();
	}
}