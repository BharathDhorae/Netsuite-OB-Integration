package com.promanatia.openbravonetsuiteintegration.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.promanatia.openbravonetsuiteintegration.dto.OrgSubsidaryDto;
import com.promanatia.openbravonetsuiteintegration.repository.OrgSubsidaryRepository;

@Service
public class LookupService {

	private final OrgSubsidaryRepository orgRepository;

	private final Map<String, OrgSubsidaryDto> organizationCache = new ConcurrentHashMap<>();
	private final Map<String, OrgSubsidaryDto> businessPartnerCache = new ConcurrentHashMap<>();
	private final Map<String, OrgSubsidaryDto> subsidiaryCache = new ConcurrentHashMap<>();
	private final Map<String, String> orgNameShipmentCache = new ConcurrentHashMap<>();
	private final Map<String, String> orgExternalInventoryCache = new ConcurrentHashMap<>();

	public LookupService(OrgSubsidaryRepository orgRepository) {

		this.orgRepository = orgRepository;
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

	public String getOrgName(String itemLineLocation) {

		if (itemLineLocation == null || itemLineLocation.isBlank()) {
			return null;
		}

		return orgNameShipmentCache.computeIfAbsent(itemLineLocation.trim(), orgRepository::getOrgNameByLocationColumn);
	}

	public String getExternalInventoryLocation(String externalInventoryLocation) {

		if (externalInventoryLocation == null || externalInventoryLocation.isBlank()) {
			return null;
		}

		return orgExternalInventoryCache.computeIfAbsent(externalInventoryLocation.trim(),
				orgRepository::findExternalInventoryLocation);
	}

	public void clearCache() {
		organizationCache.clear();
		businessPartnerCache.clear();
		subsidiaryCache.clear();
		orgNameShipmentCache.clear();
		orgExternalInventoryCache.clear();
	}
}