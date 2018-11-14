/*
 * Copyright Siemens Healthcare Diagnostics Inc, 2018.
 * Part of the SW360 Portal Project.
 *
 * SPDX-License-Identifier: EPL-1.0
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

function drawTestChart(input) {
    
    console.log(input);

    let labels, ser1, ser2, ser3, ser4, ser5 = [];
/*
    "Under Clearing By Project Team": ${project.releaseClearingStateSummary.underClearingByProjectTeam},
    "Under Clearing": ${project.releaseClearingStateSummary.underClearing},
    "New Release": ${project.releaseClearingStateSummary.newRelease},
    "Report Available": ${project.releaseClearingStateSummary.reportAvailable},
    "Approved": ${project.releaseClearingStateSummary.approved}
*/
    input.forEach( function(element) {
        labels.push(e.projectName);
        ser1.push(e.underClearingByProjectTeam);
        ser2.push(e.underClearing);
        ser3.push(e.newRelease);
        ser4.push(e.reportAvailable);
        ser5.push(e.approved);
    });

    console.log('labels', labels);
    console.log('ser1', ser1);
    console.log('ser2', ser2);
    console.log('ser3', ser3);
    console.log('ser4', ser4);
    console.log('ser5', ser5);
    
    /*
    new Chartist.Bar('#licensedebtchart', {
        labels: labels,
        series: [
            ser1, ser2, ser3, ser4, ser5
        ]
    }, {
        stackBars: true,
        horizontalBars: true,
    }).on('draw', function(data) {
        if(data.type === 'bar') {
            data.element.attr({
            style: 'stroke-width: 30px'
            });
        }
    });
    */
}
