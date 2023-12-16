import axios, {AxiosRequestConfig, AxiosResponse} from "axios";
import {ElMessage} from "element-plus";

const baseURL = 'api'

const api = axios.create({
    baseURL,
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true,
});

interface ResponseType {
    readonly code: number;
    readonly message: string;
    readonly data: object | number | Array<never> | string;
}

const MessageTypeEnum = {success: 0, error: 1}
type MessageType = keyof typeof MessageTypeEnum
type ValueOf<T> = T[keyof T]

function message(_type: ValueOf<typeof MessageTypeEnum> | ResponseType, _message?: string, options?: object) {
    if (typeof _type === "number") {
        const _filter = Object.entries(MessageTypeEnum).filter(value => value[1] === _type).pop()
        if (!_filter) {
            message(1, '不合法的数字 === ' + _type)
            return;
        }
        ElMessage({
            type: _filter[0] as MessageType,
            message: _message,
            showClose: true,
            center: true,
            ...options
        })
    } else {
        message(_type.code, _type.message, options)
    }
}

type PostRequestAvailableUrl = "basis-login/login" | "captcha" | "captcha/check"

async function postData(url: PostRequestAvailableUrl, data?: object, config?: AxiosRequestConfig): Promise<ResponseType> {
    const token = localStorage.getItem('token')
    return api.post(url,
        data,
        {
            ...config,
            headers: token !== null ? {token, ...config?.headers} : config?.headers,
        }
    ).then((value: AxiosResponse<ResponseType>) => {
        const {data} = value
        message(data)
        return data
    })
}

class Api {
    readonly baseURL = baseURL
    readonly message = message
    readonly post = postData
}

export default new Api()