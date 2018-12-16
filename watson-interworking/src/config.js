/*******************************************************************************
 * Copyright (c) 2017 Sensinov (www.sensinov.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *******************************************************************************/

const config = {
    agile: {
        host: '192.168.137.5',
        port: 8080,
        pollingPeriod: 15000,
        log: "[AGILE]:"
    },
    watson: {
        orga: 'thq3pl',
        domain : 'internetofthings.ibmcloud.com',
        application: {
            key: 'a-thq3pl-qt35bgz8ay',
            token: 'PUH4HwOir?MoB8BkvU',
            id: 'agile-watson-ipe'
        },
        log: "[WATSON]:"
    }
};

module.exports = config;
