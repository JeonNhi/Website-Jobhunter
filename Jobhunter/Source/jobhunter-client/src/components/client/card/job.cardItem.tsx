import { getLocationName } from "@/config/utils";
import { EnvironmentOutlined, ThunderboltOutlined } from "@ant-design/icons";
import { Badge, Card, Divider } from "antd";
import dayjs from "dayjs";
import React from "react";
import styles from "styles/client.module.scss";
import SkillItem from "./skill.item";

const JobCardItem = (props: any) => {
  const { item, onClick } = props;
  return (
    <Badge.Ribbon text="HOT" color="red">
      <Card size="small" title={null} hoverable onClick={() => onClick(item)}>
        <div className={styles["card-job-content"]}>
          <div className={styles["job-updatedAt"]}>
            Posted{" "}
            {item.updatedAt
              ? dayjs(item.updatedAt).locale("en").fromNow()
              : dayjs(item.createdAt).locale("en").fromNow()}
          </div>
          <div className={styles["job-title"]}>
            <span>{item.name}</span>
          </div>

          <div className={styles["card-job-info"]}>
            <div className={styles["card-job-left"]}>
              <img
                alt="example"
                src={`${import.meta.env.VITE_BACKEND_URL}/storage/company/${
                  item?.company?.logo
                }`}
              />
              <div className={styles["company-name"]}>
                <span>{item.company.name}</span>
              </div>
            </div>
            <div className={styles["company-group"]}>
              {item?.skills &&
                item?.skills?.length > 0 &&
                item.skills.map((skill: any) => (
                  <SkillItem key={skill.id} item={skill?.name} />
                ))}
            </div>
            <Divider dashed style={{ margin: "12px 0" }} />
            <div className={styles["card-job-right"]}>
              <div className={styles["job-location"]}>
                <EnvironmentOutlined style={{ color: "#58aaab" }} />
                &nbsp;{getLocationName(item.location)}
              </div>
              <div className={styles["job-salary"]}>
                <ThunderboltOutlined style={{ color: "orange" }} />
                &nbsp;
                {(item.salary + "")?.replace(/\B(?=(\d{3})+(?!\d))/g, ",")} đ
              </div>
            </div>
          </div>
        </div>
      </Card>
    </Badge.Ribbon>
  );
};

export default JobCardItem;
