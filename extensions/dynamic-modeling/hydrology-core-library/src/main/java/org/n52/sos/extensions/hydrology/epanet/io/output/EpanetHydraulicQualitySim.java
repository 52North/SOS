/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.extensions.hydrology.epanet.io.output;

import java.io.IOException;
import java.util.logging.Logger;

import org.addition.epanet.hydraulic.HydraulicSim;
import org.addition.epanet.quality.QualitySim;

import org.addition.epanet.hydraulic.io.AwareStep;
import org.addition.epanet.hydraulic.structures.SimulationControl;
import org.addition.epanet.hydraulic.structures.SimulationPump;
import org.addition.epanet.hydraulic.structures.SimulationRule;
import org.addition.epanet.hydraulic.structures.SimulationTank;
import org.addition.epanet.network.Network;
import org.addition.epanet.network.PropertiesMap;
import org.addition.epanet.util.ENException;

/**
 * Extended Hydraulic-Quality simulation class.
 */
class EpanetHydraulicQualitySim extends HydraulicSim
{
    private QualitySim qualitySim;
    
    /** Creates a new EpanetHydraulicQualitySim object. */
    public EpanetHydraulicQualitySim(Network network, Logger log) throws ENException
    {
        super(network, log);
        
        PropertiesMap propertiesMap = network.getPropertiesMap();
        qualitySim = !propertiesMap.getQualflag().equals(PropertiesMap.QualType.NONE) ? new QualitySim(network, log) : null;
    }
    
    /**
     * Finds length of next time step & updates tank levels and rule-based control actions.
     */
    @Override
    protected long nextHyd() throws ENException, IOException 
    {
        if (qualitySim!=null)
        {
            long hydstep = 0;
            
            if (simulationOutput != null)
                AwareStep.writeHydAndQual(simulationOutput, this, qualitySim, Rtime-Htime, Htime);
            
            if (Htime < pMap.getDuration())
                hydstep = timeStepEx();
            
            if (pMap.getDuration() == 0)
                SimulationPump.stepEnergy(pMap, fMap, Epat, nPumps, Htime, 0);            
            else
            if (Htime < pMap.getDuration())
                SimulationPump.stepEnergy(pMap, fMap, Epat, nPumps, Htime, hydstep);
            
            if (Htime < pMap.getDuration()) 
            {
                Htime += hydstep;
                if (Htime >= Rtime) Rtime += pMap.getRstep();
            }
            
            for (long i = 0, qstep = pMap.getQstep(), numQsteps = hydstep/pMap.getQstep(); i < numQsteps; i++)
            {
                qualitySim.simulateSingleStep(nNodes, nLinks, qstep);
            }
            return hydstep;
        }
        return super.nextHyd();
    }
    
    /**
     * Computes time step to advance hydraulic simulation (Copy of private method 'HydraulicSim::timeStep()').
     */
    private long timeStepEx() throws ENException 
    {
        long tstep = pMap.getHstep();

        long n = ((Htime + pMap.getPstart()) / pMap.getPstep()) + 1;
        long t = n * pMap.getPstep() - Htime;

        if (t > 0 && t < tstep)
            tstep = t;

        // Revise time step based on smallest time to fill or drain a tank
        t = Rtime - Htime;
        if (t > 0 && t < tstep) tstep = t;

        tstep = SimulationTank.minimumTimeStep(nTanks, tstep);
        tstep = SimulationControl.minimumTimeStep(fMap, pMap, nControls, Htime, tstep);

        if (nRules.size() > 0) 
        {
            SimulationRule.Result res = SimulationRule.minimumTimeStep(fMap, pMap, logger, nRules, nTanks, Htime, tstep, Dsystem);
            tstep = res.step;
            Htime = res.htime;
        }
        else
        {
            SimulationTank.stepWaterLevels(nTanks, fMap, tstep);
        }
        return (tstep);
    }
}
