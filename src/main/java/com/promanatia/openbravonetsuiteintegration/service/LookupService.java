package com.promanatia.openbravonetsuiteintegration.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.promanatia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promanatia.openbravonetsuiteintegration.dto.OrgWarehouseBinAccountDTO;
import com.promanatia.openbravonetsuiteintegration.repository.OrgSubsidaryRepository;
import com.promanatia.openbravonetsuiteintegration.repository.TaxRateRepository;

@Service
public class LookupService {

	private final OrgSubsidaryRepository orgRepository;
	private final TaxRateRepository taxRateRepository;

	private final Map<String, OrgSubsidaryDto> organizationCache = new ConcurrentHashMap<>();
	private final Map<String, OrgSubsidaryDto> businessPartnerCache = new ConcurrentHashMap<>();
	private final Map<String, OrgSubsidaryDto> subsidiaryCache = new ConcurrentHashMap<>();
	private final Map<String, String> orgExternalInventoryCache = new ConcurrentHashMap<>();
	private final Map<String, OrgWarehouseBinAccountDTO> orgWareouseBinAccountCache = new ConcurrentHashMap<>();
	private final Map<String, String> orgTaxRuleCache = new ConcurrentHashMap<>();

	public LookupService(OrgSubsidaryRepository orgRepository, TaxRateRepository taxRateRepository) {
		this.orgRepository = orgRepository;
		this.taxRateRepository = taxRateRepository;
	}

	public OrgSubsidaryDto getOrganization(String organization) {

		if (organization == null || organization.isBlank()) {
			return null;
		}

		return organizationCache.computeIfAbsent(organization.trim(), orgRepository::findByOrganizationName);
	}

	public OrgSubsidaryDto getBusinessPartner(String bp) {

		if (bp == null || bp.isBlank()) {
			return null;
		}

		return businessPartnerCache.computeIfAbsent(bp.trim(), orgRepository::findByBusinessPartner);
	}

	public OrgSubsidaryDto getBySubsidiary(String subsidiary, String vendorSubsidiary) {

		if (subsidiary == null || subsidiary.isBlank()) {
			return null;
		}

		String trimmedSubsidiary = subsidiary.trim();
		String trimmedVendorSubsidiary = vendorSubsidiary == null ? "" : vendorSubsidiary.trim();

		String key = trimmedSubsidiary + "|" + trimmedVendorSubsidiary;

		return subsidiaryCache.computeIfAbsent(key,
				k -> orgRepository.findBySubsidiary(trimmedSubsidiary, trimmedVendorSubsidiary));
	}

	public String getExternalInventoryLocation(String externalInventoryLocation) {

		if (externalInventoryLocation == null || externalInventoryLocation.isBlank()) {
			return null;
		}

		return orgExternalInventoryCache.computeIfAbsent(externalInventoryLocation.trim(),
				orgRepository::findExternalInventoryLocation);
	}

	public OrgWarehouseBinAccountDTO getBankAccountBySubsidiary(String subsidiary, String financialLocation) {

		if (subsidiary == null || subsidiary.isBlank()) {
			return null;
		}

		String trimmedSubsidiary = subsidiary.trim();
		String trimmedFinancialLocation = financialLocation == null ? "" : financialLocation.trim();

		String key = trimmedSubsidiary + "|" + trimmedFinancialLocation;

		return orgWareouseBinAccountCache.computeIfAbsent(key,
				k -> orgRepository.getOrgWarehousBinBySubsidiary(trimmedSubsidiary, trimmedFinancialLocation));
	}

	public String getTaxCategory(String externalId, String taxGroup) {

		if (externalId == null || externalId.isBlank()) {
			return null;
		}

		String trimmedExternalId = externalId.trim();
		String trimmedTaxGroup = taxGroup == null ? "" : taxGroup.trim();

		String key = trimmedExternalId + "|" + trimmedTaxGroup;

		return orgTaxRuleCache.computeIfAbsent(key,
				k -> taxRateRepository.getTaxCategory(trimmedExternalId, trimmedTaxGroup));
	}

	public void clearCache() {
		organizationCache.clear();
		businessPartnerCache.clear();
		subsidiaryCache.clear();
		orgExternalInventoryCache.clear();
		orgWareouseBinAccountCache.clear();
		orgTaxRuleCache.clear();
	}
}