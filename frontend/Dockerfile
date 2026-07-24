FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN npm run build

FROM node:22-alpine

WORKDIR /app

RUN npm install -g serve

COPY --from=build /app/dist/frontend/browser ./browser

EXPOSE 80

CMD ["serve", "-s", "browser", "-l", "80"]