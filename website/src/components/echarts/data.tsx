import { EChartsOption } from 'echarts'
import { BITemplate } from '@/model/sys/BITemplate'
import { EchartsRecord } from '@/model/BaseObject'
import { randomColor } from '@/utils'

// 一个基础的线模板
export const lineOptionTemplate = (legends: string[], xAxis: string[], series: any[], legendSite: string = 'bottom'): EChartsOption  => {
    return {
        tooltip: {
            trigger: 'axis'
        },
        legend: {
            // @ts-ignore
            y: legendSite,
            data: legends,
            type: 'scroll',
        },
        grid: {
            left: '3%',
            right: '5%',
            top: '12%',
            bottom: legendSite === 'bottom' ? '10%' : '0',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: xAxis
        },
        yAxis: {
            type: 'value',
            nameTextStyle: {
                padding: [0,0,10,-38]
            }
        },
        series: series
    }
}

// 柱状图
export const barOptionTemplate = (legends: string[], series: any[], unit: string = 'tCO2e'): EChartsOption  => {
    return {
        tooltip: {
            trigger: 'axis',
            formatter: '{c} ' + unit
        },
        legend: {
            left: 'center',
            bottom: '10',
            data: legends,
        },
        grid: {
            left: '2%',
            right: '2%',
            bottom: '0',
            top: 30,
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: legends,
            axisTick: {
                alignWithLabel: true,
            },
        },
        yAxis: [
            {
                type: 'value',
                name: 'tCO2e',
                nameTextStyle: {
                    padding: [0,0,0,-38]
                }
            },
        ],
        series: [
            {
                data: series,
                type: 'bar'
            }
        ],
    }
}

// 饼图
export const pieOptionTemplate = (series: any[]): EChartsOption  => {

    return {
        tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} tCO2e ({d}%)',
        },
        legend: {
            left: 'right',
            type: 'scroll',
            right: 10,
            top: 20,
            bottom: 20,
            orient: 'vertical',
        },
        series: {
            data: series,
            radius: '80%',
            center: ['50%', '50%'],
            type: 'pie'
        }
    }
}


const formatterHTMLElement = (date: string, name: string, value: string) => {
    return "<div style=\"margin: 0 0 0;line-height:1;\">\n" +
        "            <div style=\"margin: 0 0 0;line-height:1;\">\n" +
        "                <div style=\"font-size:14px;color:#fff;font-weight:400;line-height:1;\">\n" +
                            date +
        "                </div>\n" +
        "                <div style=\"margin: 10px 0 0;line-height:1;\">\n" +
        "                    <div style=\"margin: 0 0 0;line-height:1;\">\n" +
        "                        <div style=\"margin: 0 0 0;line-height:1;\">\n" +
        "                            <span style=\"display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-color:#5470c6;\">\n" +
        "                            </span>\n" +
        "                            <span style=\"font-size:14px;color:#fff;font-weight:400;margin-left:2px\">\n" +
        "                                实际排放\n" +
        "                            </span>\n" +
        "                            <span style=\"float:right;margin-left:20px;font-size:14px;color:#fff;font-weight:900\">   \n" +
                                        value +
        "                            </span>\n" +
        "                            <div style=\"clear:both\">\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                    </div>\n" +
        "                    <div style=\"clear:both\">\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "                <div style=\"clear:both\">\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>"
}

// 一个自定义可以携带单位的线
export const  createLineOptionOfPortableUnits = (legends: string[], xAxis: string[], series: any[], unit: string[]): EChartsOption  => {
    const option = lineOptionTemplate(legends,xAxis,series,'top')
    option.tooltip = {
        ...option.tooltip,
        // @ts-ignore
        formatter: (params: Array) => formatterHTMLElement(params[0].axisValue,params[0].seriesName,params[0].value + ' ' + unit[params[0].seriesIndex])
    }
    return option
}

/**
 * 一个最基本的多线图
 * @param data
 */
export const createLineOption = (data: Array<EchartsRecord | BITemplate> = []): EChartsOption => {

    let xData: string[] = []
    const series: any[] = []
    const legends: string[] = []

    if (data.length > 0) {
        xData = data[0].xdata ? data[0].xdata : []
        for (let item of data) {
            legends.push(item.name)
            series.push({
                type: 'line',
                stack: 'Total',
                smooth: true,
                data: item.data,
                name: item.name
            })
        }
    }

    return lineOptionTemplate(legends, xData, series)
}

export const createBarOption = (data: Array<EchartsRecord> = []): EChartsOption => {
    const legend: string[] = []
    const series: string[] = []
    if (data.length > 0) {
        data.forEach( item => {
            legend.push(item.name)
            series.push(item.value!.toString())
        })
    }

    return barOptionTemplate(legend, series)
}

export const createAlignLineOption = (data: Array<BITemplate> = []): EChartsOption => {
    let xData: string[] = []
    const legends: string[] = []
    const units: string[] = []
    const series: any[] = []
    const yAxisList: any[] = []
    if (data.length > 0) {
        xData = data[0].xdata!;
        data.forEach((item) => {
            legends.push(item.name)
            units.push(item.unit)
            let yAxis = yAxisList.find(axis => axis.position === item.position)
            if (!yAxis) {
                yAxis = {
                    type: 'value',
                    position: item.position,
                    name: item.name,
                    nameTextStyle: {
                        color: 'transparent'
                    }
                }
                yAxisList.push(yAxis)
            }
            series.push({
                type: 'line',
                stack: 'Total',
                smooth: true,
                data: item.data,
                name: item.name,
                yAxisIndex: yAxisList.indexOf(yAxis),
                lineStyle:{
                    type: item.name.indexOf('区间') > 0 ?  'dashed' : 'solid'
                }
            })
        })
    }

    const option = lineOptionTemplate(legends, xData, series, 'top')
    option.yAxis = yAxisList
    option.tooltip = {
        ...option.tooltip,
        formatter: function (params) {
            // @ts-ignore
            let res = params[0].name
            // @ts-ignore
            for (let i = 0; i < params.length; i++) {
                // @ts-ignore
                res += "<br>" + params[i].marker + params[i].seriesName + "：" + params[i].value + " " + units[i];
            }
            return res
        }
    }
    return option
}



/**
 * 创建一个有多个圆环进度条的对比图，用于配额，资产，碳排放对比
 */
export const createRoundMultipleBar = (data: Array<EchartsRecord>): EChartsOption => {
    const series: any[] = []
    const radiusAxis = []
    let max: number = 0
    if (data.length > 0) {
        for(let i = 0; i < data.length - 1; i++){
            let indexValue = data[i].value!
            let indexMin = i;
            for(let j = i + 1;j < data.length;j++){
                let pare =  data[j].value!
                if(indexValue > pare) {
                    indexMin = j;
                    const temp = data[i];
                    data[i] = data[indexMin];
                    data[indexMin] = temp;
                }
            }
        }
        for(let i = 0; i < data.length; i++){
            let seriesData = []
            for(let j = 0; j < data.length; j++){
                if (i === j) {
                    seriesData.push(data[i].value)
                } else {
                    seriesData.push(0)
                }
            }
            let seriesItem = {
                type: 'bar',
                data: seriesData,
                coordinateSystem: 'polar',
                name: data[i].name,
                stack: 'a',
                emphasis: {
                    focus: 'series'
                }
            }
            if (max < data[i].value!) {
                max = data[i].value!
            }
            radiusAxis.push(data[i].name)
            series.push(seriesItem)
        }
    }
    return {
        angleAxis: {
            max: max,
            startAngle: 90
        },
        tooltip: {
            position: 'top',
            trigger: 'item',
            formatter: '{a} <br/>{c} tCO2e'
        },
        radiusAxis: {
            show: false,
            type: 'category'
        },
        polar: {
            radius: [30, '60%']
        },
        series: series,
        legend: {
            show: true,
            data: radiusAxis
        }
    }
}

/**
 *  排名对比
 */
export const createMultipleBar = (data: Array<EchartsRecord>): EChartsOption => {
    let xData = []
    let legends = []
    const series: any[] = []

    if (data.length > 0) {
        for (let i = 0; i < data.length; i++) {
            const item = data[i]
            const dataList = []
            for (let j = 0; j < data.length; j++) {
                if (i === j) {
                    dataList.push(item.value)
                } else {
                    dataList.push(0)
                }
            }
            legends.push(item.name)
            xData.push(item.name)
            series.push({
                barWidth: document.body.clientWidth / 20 + 'px',
                name: xData[i],
                type: 'bar',
                data: dataList,
                barGap: '-100%'
            })
        }
    }

    return {
        yAxis: {
            axisLabel: {
                formatter: '{value}'
            },
            axisTick: {
                length: 8,
                show: false
            },
            type: 'value'
        },
        xAxis: {
            data: xData,
            name: '公司名称',
            axisLabel: {
                interval: 0,
                rotate: 0
            }
        },
        legend: {
            data: legends,
            top: 'top',
            left: 'center'
        },
        series: series,
        // @ts-ignore
        tooltip: {
            trigger: 'axis',
            // @ts-ignore
            formatter: (params: Array<any>) => {
                return params.find(item => params[0].name === item.seriesName).value + ' tCO2e'
            }
        }
    }
}

/**
 * 基础桑基图
 */
export const createBasicSankey = (data: Array<EchartsRecord>): EChartsOption => {
    let links: any[] = []
    const series: any[] = []
    const getValue = (option: any ,dataLeven: string) => {
        if (!dataLeven) return option
        let data = option
        dataLeven.split('.').filter(function (item) {
            data = data[item]
        })
        return data + ''
    }

    if (data.length > 0) {
        data.forEach( item => {
            links.push(item)
            const color = randomColor(2)
            if (!series.find( d => d.name === item.source)) {
                series.push( { name: item.source, itemStyle: { color: color, borderColor: color }, label: { color: color } } )
            }
            if (!series.find( d => d.name === item.target)) {
                series.push( { name:item.target, itemStyle: { color: color, borderColor: color }, label: { color: color } })
            }
        })
    }

    series.sort( function (item1,item2) {
        return getValue(item1,'name').localeCompare(getValue(item2,'name'), 'zh-CN')
    })

    return {
        tooltip: {
            trigger: 'item',
        },
        series: [
            {
                type: 'sankey',
                left: 50.0,
                top: 20.0,
                right: 150.0,
                bottom: 25.0,
                data: series,
                links: links,
                layout: 'none',

                lineStyle: {
                    color: 'source',
                    curveness: 0.5
                },
                label: {
                    color: 'rgb(255,255,255)',
                    fontFamily: 'Arial',
                    fontSize: 10
                }
            }
        ]
    }
}

/**
 *  3D 柱状图
 */
export const createBar3DOption = (data: Array<EchartsRecord>): EChartsOption => {
    let xAxis3DData: string[] = []
    let series: any[] = []

    if (data.length > 0) {
        xAxis3DData = data[0].xdata!
        data.forEach( item => {
            series.push(item.data)
        })
    }

    return {
        xAxis3D: {
            type: 'category',
            name: '日期',
            data: xAxis3DData
        },
        yAxis3D: {
            type: 'category',
            name: '类别',
            data: ['源', '汇']
        },
        zAxis3D: {
            type: 'value',
            name: '碳(tCO2e)'
        },
        grid3D: {
            boxWidth: 200,
            boxDepth: 80,
            light: {
                main: {
                    intensity: 1.2,
                    shadow: true
                },
                ambient: {
                    intensity: 0.3
                }
            }
        },
        series: [
            {
                // @ts-ignore
                type: 'bar3D',
                data: series,
                shading: 'lambert',
                label: {
                    fontSize: 16,
                    borderWidth: 1,
                },
                itemStyle: {
                    // @ts-ignore
                    normal: {
                        // @ts-ignore
                        color: (params: Array) => ["#ff0000", "#00ff33"][params.dataIndex % 2],
                    },
                },
            }
        ]
    }
}
/**
 * 堆叠柱形图
 */
export const createStackedHorizontalBar = (data: Array<EchartsRecord>): EChartsOption => {
    let xData: string[] = []
    const legends: string[] = []
    const series: any[] = []

    if (data.length > 0) {
        xData = data[0].xdata!
        data.forEach(item =>{
            legends.push(item.name)
            series.push( {
                stack: 'first',
                type: 'bar',
                data: item.data,
                name: item.name
            })
        })
    }
    return {
        tooltip: {
            confine: true,
            trigger: 'axis'
        },
        legend: {
            type: 'scroll',
            data: legends,
            top: 'top',
            left: 'center',
            width: '90%',
            textStyle: {
                color: 'rgb(0,205,255)'
            }
        },
        grid: {
            left: '2%',
            right: '2%',
            bottom: '0',
            top: 55,
            containLabel: true
        },
        xAxis: {
            data: xData
        },
        yAxis: {
            axisTick: {
                length: 8,
                show: false,
            },
            name: 'tCO2e',
            type: 'value'
        },
        series: series,
    }
}

// 雨量关系图
export const createCarbonPriceLine = (data: Array<EchartsRecord>): EChartsOption => {
    let seriesData: any[] = []
    let legendData: string[] = []

    data.forEach((item, index) => {
        let itemDatas = []
        let itemDatas2 = []
        for (let i = 0; i < item.data.length; i++) {
            const data = item.data[i]
            if (data != 0) {
                itemDatas.push(data)
            } else {
                itemDatas.push(null)
            }
        }
        for (let i = 0; i < item.data2!.length; i++) {
            let data = item.data2![i]
            if (data != 0) {
                itemDatas2.push(data)
            } else {
                itemDatas2.push(null)
            }
        }
        seriesData.push({
            name: item.name,
            type: 'line',
            data: itemDatas
        })
        seriesData.push({
            name: item.name,
            type: 'line',
            xAxisIndex: 1,
            yAxisIndex: 1,
            data: itemDatas2,
        })
        legendData.push(item.name)
    })

    return {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                animation: false,
            },
            // @ts-ignore
            formatter: (params: Array) => {
                const params1 = params.filter((item: any) => item.axisIndex == 0)
                const params2 = params.filter((item: any) => item.axisIndex == 1)
                let str = ''
                str = str + params1[0].axisValueLabel + '/单价'
                params1.forEach((item: any) => {
                    if (item.data != null) {
                        str = str + '<br>'
                        str = str + item.marker;
                        str = str + item.seriesName + ': '
                        str = str + item.data;
                        str = str + '元'
                    }
                })
                str = str + '<br><br>'
                str = str + params2[0].axisValueLabel + '/交易量'
                params2.forEach((item: any) => {
                    if (item.data != null) {
                        str = str + '<br>'
                        str = str + item.marker;
                        str = str + item.seriesName + ': '
                        str = str + item.data;
                        str = str + '吨'
                    }
                })
                return str
            }
        },
        legend: {
            type: 'scroll',
            top: 'top',
            left: 'center',
            width: '90%',
            data: legendData
        },
        axisPointer: {
            link: [
                {
                    xAxisIndex: 'all'
                }
            ]
        },
        grid: [
            {
                left: 60,
                right: 50,
                height: '35%'
            },
            {
                left: 60,
                right: 50,
                top: '55%',
                height: '35%'
            }
        ],
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                axisLine: { onZero: true },
                data: data[0].xdata
            },
            {
                gridIndex: 1,
                type: 'category',
                boundaryGap: false,
                axisLine: { onZero: true },
                data: data[0].xdata,
                position: 'top'
            },
        ],
        yAxis: [
            {
                name: '￥',
                type: 'value',
                max: 500,
            },
            {
                gridIndex: 1,
                name: 'T',
                type: 'value',
                inverse: true
            },
        ],
        series: seriesData
    }
}

