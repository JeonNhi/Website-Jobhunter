import { Badge, Card, Divider } from "antd";
import React, { Fragment } from "react";
import styles from "styles/client.module.scss";
import bg from "../../../assets/images/19366.jpg";
import SkillItem from "./skill.item";
import { RightOutlined } from "@ant-design/icons";
import "../../../styles/client.module.scss";
const CompanyCardItem = (props: any) => {
  const { onClick, item } = props;
  return (
    <Badge.Ribbon
      text={item?.jobs && item?.jobs?.length > 0 ? "Actives" : "Unavailable"}
      color={item?.jobs && item?.jobs?.length > 0 ? "red" : "green"}
    >
      <Card
        onClick={() => onClick(item)}
        style={{ background: "#f6fafd" }}
        hoverable
        cover={
          <div className={styles["card-customize"]}>
            <img className={styles["img-bg-card"]} src={bg} />
            <div className={styles["group-img-company"]}>
              <img
                className={styles["img-company"]}
                alt="example"
                src={`${import.meta.env.VITE_BACKEND_URL}/storage/company/${
                  item?.logo
                }`}
              />
              <h3 style={{ textAlign: "center" }}>{item.name}</h3>

              <div className={styles["skill-group"]}>
                {item?.skills &&
                  item?.skills?.length > 0 &&
                  item.skills.map((skill: any) => (
                    <SkillItem key={skill.id} item={skill?.name} />
                  ))}
              </div>
            </div>
          </div>
        }
      >
        <div className={styles["group-info-company"]}>
          <div className={styles["address-company"]}>
            <h3>{item.address}</h3>
          </div>
          <div className={styles["info-job"]}>
            {(item?.jobs && item?.jobs?.length) || 0} Jobs
            <RightOutlined
              style={{ width: "12px", fontWeight: "600", marginTop: "4px" }}
            />
          </div>
        </div>
      </Card>
    </Badge.Ribbon>
  );
};

export default CompanyCardItem;
