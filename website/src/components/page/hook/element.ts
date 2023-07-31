import { t } from '@/i18n'

export const InitElement = "<div class=\"init-tour-element\">\n" +
    "            <div class=\"init-left\">\n" +
    "                <span class=\"title\">" + t('tour.initElement.welcomeToUse') + "</span>\n" +
    "                <p>" + t('tour.initElement.welcomeDoc') + "</p>\n" +
    "            </div>\n" +
    "            <div class=\"init-right\">\n" +
    "                <span class=\"title\">\n" +
                        t('tour.initElement.title') +
    "                </span>\n" +
    "                <div id=\"box-items\" class=\"box-items\">\n" +
    "                    <div key=\"/sys/enterprise\" class=\"box activity-box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(42, 130, 228, 1)\">\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                            <path d=\"M24 14V6H4v36h40V14H24zM12 38H8v-4h4v4zm0-8H8v-4h4v4zm0-8H8v-4h4v4zm0-8H8v-4h4v4zm8 24h-4v-4h4v4zm0-8h-4v-4h4v4zm0-8h-4v-4h4v4zm0-8h-4v-4h4v4zm20 24H24v-4h4v-4h-4v-4h4v-4h-4v-4h16v20zm-4-16h-4v4h4v-4zm0 8h-4v4h4v-4z\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.enterprise') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/sys/role\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path d=\"M24 24c4.42 0 8-3.59 8-8 0-4.42-3.58-8-8-8s-8 3.58-8 8c0 4.41 3.58 8 8 8zm0 4c-5.33 0-16 2.67-16 8v4h32v-4c0-5.33-10.67-8-16-8z\"></path>\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.role') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/sys/user\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path d=\"M24 4C12.95 4 4 12.95 4 24s8.95 20 20 20 20-8.95 20-20S35.05 4 24 4zm0 6c3.31 0 6 2.69 6 6 0 3.32-2.69 6-6 6s-6-2.68-6-6c0-3.31 2.69-6 6-6zm0 28.4c-5.01 0-9.41-2.56-12-6.44.05-3.97 8.01-6.16 12-6.16s11.94 2.19 12 6.16c-2.59 3.88-6.99 6.44-12 6.44z\"></path>\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.user') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/plan/emissionSource\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                            <path d=\"M10 4c0-1.1-.89-2-2-2s-2 .9-2 2v8H2v12h12V12h-4V4zm8 28c0 2.61 1.68 4.81 4 5.63V46h4v-8.37c2.32-.83 4-3.02 4-5.63v-4H18v4zM2 32c0 2.61 1.68 4.81 4 5.63V46h4v-8.37c2.32-.83 4-3.02 4-5.63v-4H2v4zm40-20V4c0-1.1-.89-2-2-2s-2 .9-2 2v8h-4v12h12V12h-4zM26 4c0-1.1-.89-2-2-2s-2 .9-2 2v8h-4v12h12V12h-4V4zm8 28c0 2.61 1.68 4.81 4 5.63V46h4v-8.37c2.32-.83 4-3.02 4-5.63v-4H34v4z\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.emissionSource') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/plan/gwp\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path fill=\"none\" d=\"M0 0h48v48H0z\"></path>\n" +
    "                            <circle cx=\"14.4\" cy=\"28.8\" r=\"6.4\"></circle>\n" +
    "                            <circle cx=\"29.6\" cy=\"36\" r=\"4\"></circle>\n" +
    "                            <circle cx=\"30.4\" cy=\"17.6\" r=\"9.6\"></circle>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.gwp') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/plan/fac/index\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path d=\"M14 48h4v-4h-4v4zm8 0h4v-4h-4v4zm8 0h4v-4h-4v4zM32 .02L16 0c-2.21 0-4 1.79-4 4v32c0 2.21 1.79 4 4 4h16c2.21 0 4-1.79 4-4V4c0-2.21-1.79-3.98-4-3.98zM32 32H16V8h16v24z\"></path>\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.fac') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/plan/version\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                            <path d=\"M46 24l-4.88-5.56.68-7.37-7.22-1.63-3.78-6.36L24 6l-6.8-2.92-3.78 6.36-7.22 1.63.68 7.37L2 24l4.88 5.56-.68 7.37 7.22 1.63 3.78 6.36L24 42l6.8 2.92 3.78-6.36 7.22-1.63-.68-7.37L46 24zM26 34h-4v-4h4v4zm0-8h-4V14h4v12z\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.version') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/plan/versionData\" class=\"box\">\n" +
    "                        <svg viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"6899\" width=\"35\" height=\"35\">\n" +
    "                            <path\n" +
    "                                d=\"M513.525238 0C231.624846 0 3.069407 228.498134 3.069407 510.454808s228.555439 510.454808 510.454808 510.454808c281.898345 0 510.453784-228.556463 510.453784-510.454808C1023.979022 228.556463 795.42256 0 513.525238 0zM764.639462 385.394511C684.634314 493.316475 604.620979 601.369424 524.614807 709.290365c-16.693194 22.560832-55.583946 38.817074-83.120093 19.750833-57.577347-39.756469-115.089202-79.502705-172.601056-119.200846-26.301016-18.192338-40.125883-47.228651-21.809725-75.580373 15.202237-23.61586 56.704466-38.075177 83.186607-19.816325 57.511855 39.688931 127.233799 87.923492 127.233799 87.923492 67.358107-90.913593 134.658909-181.829232 202.081485-272.677333C703.074292 270.988878 808.506553 326.201363 764.639462 385.394511z\"\n" +
    "                                p-id=\"6900\" fill=\"#505050\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.versionData') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/entry/baseBurn\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                            <path d=\"M42 6H6c-2.21 0-4 1.79-4 4v24c0 2.21 1.79 4 4 4h10v4h16v-4h10c2.21 0 3.98-1.79 3.98-4L46 10c0-2.21-1.79-4-4-4zm0 28H6V10h36v24zm-4-18H16v4h22v-4zm0 8H16v4h22v-4zm-24-8h-4v4h4v-4zm0 8h-4v4h4v-4z\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.baseBurn') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/entry/factoryBurn\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                            <path d=\"M20.17 31.17L23 34l10-10-10-10-2.83 2.83L25.34 22H6v4h19.34l-5.17 5.17zM38 6H10c-2.21 0-4 1.79-4 4v8h4v-8h28v28H10v-8H6v8c0 2.21 1.79 4 4 4h28c2.21 0 4-1.79 4-4V10c0-2.21-1.79-4-4-4z\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.factoryBurn') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/entry/activityBurn\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path d=\"M10 26h28v-4H10v4zm-4 8h28v-4H6v4zm8-20v4h28v-4H14z\"></path>\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.activityBurn') + "</span>\n" +
    "                    </div>\n" +
    "                    <div key=\"/report/approve/apply\" class=\"box\">\n" +
    "                        <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"40\" height=\"40\" viewBox=\"0 0 48 48\" fill=\"rgba(80, 80, 80, 1)\">\n" +
    "                            <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
    "                            <path d=\"M38 6h-8.37c-.82-2.32-3.02-4-5.63-4s-4.81 1.68-5.63 4H10c-2.21 0-4 1.79-4 4v28c0 2.21 1.79 4 4 4h28c2.21 0 4-1.79 4-4V10c0-2.21-1.79-4-4-4zM24 6c1.1 0 2 .89 2 2s-.9 2-2 2-2-.89-2-2 .9-2 2-2zm-4 28l-8-8 2.83-2.83L20 28.34l13.17-13.17L36 18 20 34z\"></path>\n" +
    "                        </svg>\n" +
    "                        <span>" + t('tour.initElement.approve') + "</span>\n" +
    "                    </div>\n" +
    "                </div>\n" +
    " <span class=\"driver-next-btn\">" + t('tour.next') + "</span>" +
    "            </div>\n" +
    "        </div>"