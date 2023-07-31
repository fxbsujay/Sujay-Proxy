import dayjs, { Dayjs } from 'dayjs'

export type RangeValue = [Dayjs, Dayjs]

export function getYearStartTime(): Dayjs {
   return  dayjs().startOf('year')
}

export function getYearStartTimeString(): string {
   return  dayjs().startOf('year').format('YYYY-MM-DD HH:mm:ss')
}


export function defaultFormat(date: string, format: string = 'YYYY-MM-DD'): string {
   return dayjs(date,'YYYY-MM-DD HH:mm:ss').format(format)
}

export function getPreYearString(): string {
   return dayjs().subtract(1,'year').format('YYYY-MM-DD HH:mm:ss')
}

export function getNowString(): string {
   return dayjs().format('YYYY-MM-DD HH:mm:ss')
}