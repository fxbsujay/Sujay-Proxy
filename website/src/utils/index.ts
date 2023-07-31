
export const rinseTree = (tree: any[]) => {
    if (tree.length <= 0) {
        return
    }
    for (let element of tree) {
        if (element.children && element.children.length > 0) {
            rinseTree(element.children)
        } else {
            delete element['children']
        }
    }
}

export function isDate(val : any) : boolean {
    return Object.prototype.toString.call(val) === '[object.Date]'
}

export function isObject(val : any) : boolean {
    return val !== null && typeof val === 'object'
}

export function isNotNull(val : any) : boolean {
    if (typeof val !== 'undefined' && val !== null) {
        if (typeof val == 'string') {
            return val.length > 0
        }
        return true
    }
    return false
}

/**
 * 解码
 */
function encode(val : string) : string {
    return encodeURIComponent(val)
        .replace(/%40/g, '@')
        .replace(/%3A/ig, ':')
        .replace(/%24/g, '$')
        .replace(/%2C/ig, ',')
        .replace(/%20/g, '+')
        .replace(/%5B/ig, '[')
        .replace(/%5D/ig , ']')
}

/**
 * 根据路径和参数构建请求URL
 */
export const buildURL = (url: string, params?: any): string => {
    if (!params){
        return url
    }

    const parts : string[] = []

    Object.keys(params).forEach((key) => {
        const val = params[key]
        if (val === null || typeof val === 'undefined'){
            return
        }
        let values = []
        if(Array.isArray(val)) {
            values = val
            key += '[]'
        }else{
            values = [val]
        }

        values.forEach((val) => {
            if(isDate(val)) {
                val = val.toISOString()
            }else if (isObject(val)) {
                val = JSON.stringify(val)
            }
            parts.push(`${encode(key)}=${encode(val)}`)
        })
    })
    let serializedParams = parts.join('&')

    if (serializedParams) {
        const markIndex = url.indexOf('#')
        if (markIndex !== -1) {
            url = url.slice(0,markIndex)
        }
        url += (url.indexOf('?') === -1 ? '?' : '&') + serializedParams
    }

    return url
}

/**
 * 文件转换
 */
export function getBase64(img: Blob, callback: (base64Url: string) => void) {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result as string));
    reader.readAsDataURL(img);
}

/**
 * 随机生成Id
 */
export function generateID() {
    return Number(Math.random().toString().substr(2,8) + Date.now()).toString(36)
}

/**
 * 随机获取一种颜色
 */
export function randomColor(type: number) {
    let colorArr: string[] = []
    if(!type) {
        type = Math.ceil(Math.random()*3);
    }
    let res
    switch (type) {
        case 1:
            colorArr = ["AliceBlue", "AntiqueWhite", "Aqua", "Aquamarine", "Azure", "Beige", "Bisque", "Black","BlanchedAlmond", "Blue", "BlueViolet", "Brown", "BurlyWood", "CadetBlue", "Chartreuse", "Chocolate","Coral", "CornflowerBlue", "Cornsilk", "Crimson", "Cyan", "DarkBlue", "DarkCyan", "DarkGoldenRod","DarkGray","DarkGreen","DarkKhaki","DarkMagenta","DarkOliveGreen", "DarkOrange","DarkOrchid","DarkRed","DarkSalmon","DarkSeaGreen","DarkSlateBlue","DarkSlateGray","DarkTurquoise","DarkViolet","DeepPink","DeepSkyBlue","DimGray","DodgerBlue","FireBrick","FloralWhite","ForestGreen","Fuchsia","Gainsboro","GhostWhite","Gold","GoldenRod","Gray","Green","GreenYellow","HoneyDew","HotPink","IndianRed","Indigo","Ivory","Khaki","Lavender","LavenderBlush","LawnGreen", "LemonChiffon","LightBlue","LightCoral","LightCyan","LightGoldenRodYellow","LightGray","LightGreen","LightPink","LightSalmon","LightSeaGreen","LightSkyBlue","LightSlateGray","LightSteelBlue","LightYellow","Lime" ,"LimeGreen","Linen","Magenta","Maroon","MediumAquaMarine","MediumBlue","MediumOrchid","MediumPurple","MediumSeaGreen","MediumSlateBlue","MediumSpringGreen","MediumTurquoise","MediumVioletRed","MidnightBlue","MintCream","MistyRose","Moccasin","NavajoWhite","Navy","OldLace","Olive","OliveDrab","Orange","OrangeRed","Orchid","PaleGoldenRod","PaleGreen","PaleTurquoise","PaleVioletRed","PapayaWhip","PeachPuff","Peru","Pink","Plum","PowderBlue","Purple","Red","RosyBrown","RoyalBlue","SaddleBrown","Salmon","SandyBrown","SeaGreen","SeaShell","Sienna","Silver","SkyBlue","SlateBlue","SlateGray","Snow","SpringGreen","SteelBlue","Tan","Teal","Thistle","Tomato","Turquoise","Violet","Wheat","White","WhiteSmoke","Yellow","YellowGreen"];
            res = colorArr[Math.floor(Math.random() * colorArr.length)]
            if (!isNotNull(res)) {
                randomColor(type)
            }
            break
        case 2:
            colorArr = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'];
            res = "#"
            for (let i = 0; i < 6; i++) {
                res += colorArr[Math.floor(Math.random() * colorArr.length)]
            }
            if (res.length !== 7) {
                randomColor(type)
            }
            break
        case 3:
            for (let j = 0; j < 3; j++) {
                let c = Math.random() * 256;
                if (c >= 255) {
                    c = 255;
                }
                colorArr.push(c.toString());
            }
            if (colorArr.length !== 3) {
                randomColor(type);
            }
            res = "rgb(" + colorArr[0] + "," + colorArr[1] + "," + colorArr[2] + ")"
            break
        case 4:
            for (let m = 0; m < 4; m++) {
                let d = 0;
                if (m < 3) {
                    d = Math.random() * 256;
                    if (d >= 255) {
                        d = 255;
                    }
                } else {
                    d = Math.random() * 2;
                    if (d >= 1) {
                        d = 1;
                    }
                }
                colorArr.push(d.toString());
            }
            if (colorArr.length !== 4) {
                randomColor(type);
            }
            res = "rgba(" + colorArr[0] + "," + colorArr[1] + "," + colorArr[2] + "," + colorArr[3] + ")"
            break
        case 5:
            colorArr = ['#87EC58', '#42EF5F', '#15D8E2', '#3EA8EF', '#A5F65A', '#35DE46', '#38D2ED', '#EA51E5', '#C76BA5','#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#ea7ccc']
            res = colorArr[Math.floor(Math.random() * colorArr.length)]
            break
        default:
            break
    }
    return res;
}


