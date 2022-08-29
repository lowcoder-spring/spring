/*
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* global SBA */
import BusRefreshDestination from './call/bus-refresh-destination';
import BusRefresh from './call/bus-refresh';

// tag::customization-ui-toplevel[]

// end::customization-ui-toplevel[]

// tag::customization-ui-endpoint[]
SBA.use({
    install({viewRegistry, vueI18n}) {
        viewRegistry.addView({
            name: 'instances/bus-refresh-destination',
            parent: 'instances',
            path: 'bus-refresh-destination',
            label: 'BusRefreshDestination',
            group: 'Call',
            component: BusRefreshDestination,
            order: 10000,
            isEnabled: ({instance}) => instance.hasEndpoint('bus-refresh')
        });

        viewRegistry.addView({
            name: 'instances/bus-refresh',
            parent: 'instances',
            path: 'bus-refresh',
            label: 'BusRefresh',
            group: 'Call',
            component: BusRefresh,
            order: 10000,
            isEnabled: ({instance}) => instance.hasEndpoint('bus-refresh')
        });

        vueI18n.mergeLocaleMessage('en', {
            sidebar: {
                Call : {
                    title : "Call"
                }
            }
        });
        vueI18n.mergeLocaleMessage('zh', {
            sidebar: {
                Call : {
                    title : "调用"
                }
            }
        });
    }
});
// end::customization-ui-endpoint[]