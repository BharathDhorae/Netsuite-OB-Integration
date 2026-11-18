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

		String key = subsidiary.trim() + "|" + (vendorSubsidiary == null ? "" : vendorSubsidiary.trim());

		return subsidiaryCache.computeIfAbsent(key,
				k -> orgRepository.findBySubsidiary(subsidiary.trim(), vendorSubsidiary.trim()));
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

		String key = subsidiary.trim() + "|" + (financialLocation == null ? "" : financialLocation.trim());

		return orgWareouseBinAccountCache.computeIfAbsent(key,
				k -> orgRepository.getOrgWarehousBinBySubsidiary(subsidiary.trim(), financialLocation.trim()));
	}

	public String getTaxCategory(String externalId, String taxGroup) {

		if (externalId == null || externalId.isBlank()) {
			return null;
		}

		String key = externalId.trim() + "|" + (taxGroup == null ? "" : taxGroup.trim());

		return orgTaxRuleCache.computeIfAbsent(key, k -> taxRateRepository.getTaxCategory(externalId.trim(), taxGroup));
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