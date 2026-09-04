package com.promanatia.CamelDemo.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.promanatia.CamelDemo.DTO.OrgSubsidaryDto;
import com.promanatia.CamelDemo.DTO.ProductMasterDTO;
import com.promanatia.CamelDemo.repository.OrgSubsidaryRepository;
import com.promanatia.CamelDemo.repository.ProductMasterRepository;

@Service
public class LookupService {

	private final OrgSubsidaryRepository orgRepository;
	private final ProductMasterRepository productRepository;

	private final Map<String, OrgSubsidaryDto> organizationCache = new ConcurrentHashMap<>();
	private final Map<String, OrgSubsidaryDto> businessPartnerCache = new ConcurrentHashMap<>();
	private final Map<String, ProductMasterDTO> productCache = new ConcurrentHashMap<>();
	private final Map<String, String> orgSubsidaryCache = new ConcurrentHashMap<>();
	private final Map<String, OrgSubsidaryDto> subsidiaryCache = new ConcurrentHashMap<>();

	public LookupService(OrgSubsidaryRepository orgRepository, ProductMasterRepository productRepository) {

		this.orgRepository = orgRepository;
		this.productRepository = productRepository;
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

	public ProductMasterDTO getProduct(String searchKey) {

		if (searchKey == null || searchKey.isBlank()) {
			return null;
		}

		return productCache.computeIfAbsent(searchKey.trim(), productRepository::findByProductSearchKey);
	}

	public String getOrganizationBySusidary(String subsidary) {

		if (subsidary == null || subsidary.isBlank()) {
			return null;
		}

		return orgSubsidaryCache.computeIfAbsent(subsidary.trim(), orgRepository::findBySubsdiaryForProduct);
	}

	public void clearCache() {
		organizationCache.clear();
		businessPartnerCache.clear();
		productCache.clear();
		subsidiaryCache.clear();
		orgSubsidaryCache.clear();
	}
}