#Tourine: Hystrix inspired reporter for Metrics [![Build Status](https://secure.travis-ci.org/pdavidson/tourine.png)](http://travis-ci.org/pdavidson/tourine)

[Metrics](https://github.com/dropwizard/metrics) provides valuable realtime and historical data.
[Hystrix Dashboard](https://github.com/Netflix/Hystrix/wiki/Dashboard) provide a simple realtime view into hystrix commands.
Tourine exposes Metrics as a "Hystrix Commands" in a Hystrix event stream for use with the Hystrix Dashboard.

Using [Turbine](https://github.com/Netflix/Turbine), the Tourine feed can been aggregated with the Hystrix Event stream
to provide a consolidated view.

## Full Documentation

See [Wiki](https://github.com/pdavidson/tourine/wiki) for full documentation and examples.

##License
Copyright 2014 Peter Davidson, pdavidson.us

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.