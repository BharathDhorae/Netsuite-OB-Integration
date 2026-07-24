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

	public LookupService(OrgSubsidaryRepository orgRepository, ProductMasterRepository productRepository) {

		this.orgRepository = orgRepository;
		this.productRepository = productRepository;
	}

	public OrgSubsidaryDto getOrganization(String organization) {

		if (organization == null || organization.isBlank()) {
			return null;
		}

		return organizationCache.computeIfAbsent(organization.trim(), orgRepository::findByEntityName);
	}

	public OrgSubsidaryDto getBusinessPartner(String bp) {

		if (bp == null || bp.isBlank()) {
			return null;
		}

		return businessPartnerCache.computeIfAbsent(bp.trim(), orgRepository::findByBusinessPartner);
	}

	public ProductMasterDTO getProduct(String searchKey) {

		if (searchKey == null || searchKey.isBlank()) {
			return null;
		}

		return productCache.computeIfAbsent(searchKey.trim(), productRepository::findByEntityName);
	}

	public void clearCache() {
		organizationCache.clear();
		businessPartnerCache.clear();
		productCache.clear();
	}

	/**
	 * Individual cache clear methods
	 */
	public void clearOrganizationCache() {
		organizationCache.clear();
	}

	public void clearBusinessPartnerCache() {
		businessPartnerCache.clear();
	}

	public void clearProductCache() {
		productCache.clear();
	}
}