package org.molgenis.app;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.molgenis.app.controller.HomeController;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.auth.UserAuthority;
import org.molgenis.data.DataService;
import org.molgenis.data.annotation.entity.impl.CaddAnnotator;
import org.molgenis.data.annotation.entity.impl.SnpEffAnnotator;
import org.molgenis.data.annotation.impl.ClinVarServiceAnnotator;
import org.molgenis.data.annotation.impl.DbnsfpGeneServiceAnnotator;
import org.molgenis.data.annotation.impl.DbnsfpVariantServiceAnnotator;
import org.molgenis.data.annotation.provider.CgdDataProvider;
import org.molgenis.data.annotation.provider.HgncLocationsProvider;
import org.molgenis.data.annotation.provider.HpoMappingProvider;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.framework.db.WebAppDatabasePopulatorService;
import org.molgenis.security.MolgenisSecurityWebAppDatabasePopulatorService;
import org.molgenis.security.core.runas.RunAsSystem;
import org.molgenis.security.core.utils.SecurityUtils;
import org.molgenis.system.core.RuntimeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebAppDatabasePopulatorServiceImpl implements WebAppDatabasePopulatorService
{
	private final DataService dataService;
	private final MolgenisSecurityWebAppDatabasePopulatorService molgenisSecurityWebAppDatabasePopulatorService;

	@Autowired
	public WebAppDatabasePopulatorServiceImpl(DataService dataService,
			MolgenisSecurityWebAppDatabasePopulatorService molgenisSecurityWebAppDatabasePopulatorService)
	{
		if (dataService == null) throw new IllegalArgumentException("DataService is null");
		this.dataService = dataService;

		if (molgenisSecurityWebAppDatabasePopulatorService == null)
			throw new IllegalArgumentException("MolgenisSecurityWebAppDatabasePopulator is null");
		this.molgenisSecurityWebAppDatabasePopulatorService = molgenisSecurityWebAppDatabasePopulatorService;

	}

	@Override
	@Transactional
	@RunAsSystem
	public void populateDatabase()
	{
		molgenisSecurityWebAppDatabasePopulatorService.populateDatabase(this.dataService, HomeController.ID);

		// Genomebrowser stuff
		Map<String, String> runtimePropertyMap = new HashMap<String, String>();

		// Annotators include files/tools
		String molgenisHomeDir = System.getProperty("molgenis.home");

		if (molgenisHomeDir == null)
		{
			throw new IllegalArgumentException("missing required java system property 'molgenis.home'");
		}

		// HPO and HGNC Download URLs
		runtimePropertyMap.put(HpoMappingProvider.KEY_HPO_MAPPING, HpoMappingProvider.DEFAULT_HPO_MAPPING_VALUE);
		runtimePropertyMap.put(HgncLocationsProvider.KEY_HGNC_LOCATIONS_VALUE,
				HgncLocationsProvider.DEFAULT_HGNC_LOCATIONS_VALUE);

		if (!molgenisHomeDir.endsWith("/")) molgenisHomeDir = molgenisHomeDir + '/';
		String molgenisHomeDirAnnotationResources = molgenisHomeDir + "data/annotation_resources";

		runtimePropertyMap.put(CaddAnnotator.CADD_FILE_LOCATION_PROPERTY,
				molgenisHomeDirAnnotationResources + "/CADD/1000G.vcf.gz");
		runtimePropertyMap.put(CgdDataProvider.CGD_FILE_LOCATION_PROPERTY,
				molgenisHomeDirAnnotationResources + "/CGD/CGD.txt");
		runtimePropertyMap.put(DbnsfpGeneServiceAnnotator.GENE_FILE_LOCATION_PROPERTY,
				molgenisHomeDirAnnotationResources + "/dbnsfp/dbNSFP2.3_gene");
		runtimePropertyMap.put(DbnsfpVariantServiceAnnotator.CHROMOSOME_FILE_LOCATION_PROPERTY,
				molgenisHomeDirAnnotationResources + "/dbnsfp/dbNSFP2.3_variant.chr");
		runtimePropertyMap.put(ClinVarServiceAnnotator.CLINVAR_FILE_LOCATION_PROPERTY,
				molgenisHomeDirAnnotationResources + "/Clinvar/variant_summary.txt");
		runtimePropertyMap.put(SnpEffAnnotator.SNPEFF_JAR_LOCATION_PROPERTY,
				molgenisHomeDirAnnotationResources + "/Applications/snpEff/snpEff.jar");

		for (Entry<String, String> entry : runtimePropertyMap.entrySet())
		{
			RuntimeProperty runtimeProperty = new RuntimeProperty();
			String propertyKey = entry.getKey();
			runtimeProperty.setName(propertyKey);
			runtimeProperty.setValue(entry.getValue());
			dataService.add(RuntimeProperty.ENTITY_NAME, runtimeProperty);
		}

		MolgenisUser anonymousUser = molgenisSecurityWebAppDatabasePopulatorService.getAnonymousUser();
		UserAuthority anonymousHomeAuthority = new UserAuthority();
		anonymousHomeAuthority.setMolgenisUser(anonymousUser);
		anonymousHomeAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_WRITE_PREFIX + HomeController.ID.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, anonymousHomeAuthority);
	}

	@Override
	@Transactional
	@RunAsSystem
	public boolean isDatabasePopulated()
	{
		return dataService.count(MolgenisUser.ENTITY_NAME, new QueryImpl()) > 0;
	}
}