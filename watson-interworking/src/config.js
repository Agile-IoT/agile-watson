/*******************************************************************************
 * Copyright (c) 2017 Sensinov (www.sensinov.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *******************************************************************************/

const config = {
    agile: {
        host: 'XXX.XXX.XXX.XXX',
        port: XXXX,
        pollingPeriod: 15000,
        log: "[AGILE]:"
    },
    watson: {
        orga: 'XXXX',
        domain : 'internetofthings.ibmcloud.com',
        application: {
            key: 'XXXXXXXXX',
            token: 'XXXXXXXXXXXXXXX',
            id: 'agile-watson-ipe'
        },
        log: "[WATSON]:"
    }
};

module.exports = config;
