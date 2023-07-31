/**
 * <p>对象的深拷贝和浅拷贝</p>
 * @author fxbsujay@gmail.com
 * @version 14:47 2023/03/14
 */
export default class Copy {
    /**
     * 判断对象是否为数组
     * @param obj
     * @returns
     */
    private static IsArray(obj: any) {
        return this.IsObject(obj) && obj instanceof Array;
    }

    private static IsObject(obj: any) {
        return obj && typeof obj == "object";
    }

    public static JsonClone<T>(tSource: T): T {
        return JSON.parse(JSON.stringify(tSource)) as T
    }

    /**
     * 对象深拷贝
     * @param tSource
     * @param tTarget
     * @returns
     */
    public static DeepClone<T>(tSource: T, tTarget?: Record<string, any> | T): T {
        if (this.IsArray(tSource)) {
            tTarget = tTarget || [];
        } else {
            tTarget = tTarget || {};
        }

        for (const key in tSource) {

            if (Object.prototype.hasOwnProperty.call(tSource, key)) {
                if (typeof tSource[key] === "object" && tSource[key] !== null) {
                    tTarget[key] = this.IsArray(tSource[key]) ? [] : {};
                    this.DeepClone(tSource[key], tTarget[key]);
                } else {
                    tTarget[key] = tSource[key];
                }
            }
        }
        return tTarget as T;
    }

    /**
     * 对象深拷贝 不追加属性
     * @param tSource
     * @param tTarget
     * @returns
     */
    public static UnCoverSimpleClone<T>(tSource:  any, tTarget?: Record<string, any> | T): T {
        if (this.IsArray(tSource)) {
            tTarget = tTarget || [];
        } else {
            tTarget = tTarget || {};
        }
        for (const key in tTarget) {

            if (Object.prototype.hasOwnProperty.call(tSource, key)) {
                tTarget[key] = tSource[key]
            } else {
                delete tTarget[key]
            }
        }
        return tTarget as T;
    }

    /**
     * 对象浅拷贝
     * @param tSource
     * @param tTarget
     * @returns
     */
    public static SimpleClone<T>(tSource: T, tTarget?: Record<string, any> | T): T {
        if (this.IsArray(tSource)) {
            tTarget = tTarget || [];
        } else {
            tTarget = tTarget || {};
        }
        for (const key in tSource) {
            if (Object.prototype.hasOwnProperty.call(tSource, key)) {
                tTarget[key] = tSource[key];
            }
        }
        return tTarget as T;
    }
}
